import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export interface GamificationNotification {
  id: string;
  type: 'ESSENCE_GAINED' | 'LEVEL_UP' | 'ACHIEVEMENT_UNLOCKED' | 'REWARD_UNLOCKED';
  title: string;
  message: string;
  icon: string;
  essenceAmount?: number;
  newLevel?: number;
  achievementId?: string;
  achievementName?: string;
  rewardId?: string;
  rewardName?: string;
  timestamp: string;
  read: boolean;
}

export type NotificationCallback = (notification: GamificationNotification) => void;

class WebSocketService {
  private client: Client | null = null;
  private connected: boolean = false;
  private connecting: boolean = false;
  private callbacks: NotificationCallback[] = [];
  private reconnectAttempts: number = 0;
  private maxReconnectAttempts: number = 5;
  private reconnectDelay: number = 5000; // Increased to 5 seconds
  private reconnectTimeout: ReturnType<typeof setTimeout> | null = null;
  private currentUserId: string | null = null;
  private currentDatabaseId: number | null = null;

  connect(firebaseUid: string, databaseUserId: number) {
    if (this.connected || this.connecting) {
      console.log('⚠️ WebSocket already connected or connecting');
      return;
    }

    this.currentUserId = firebaseUid;
    this.currentDatabaseId = databaseUserId;
    this.connecting = true;
    const socketUrl = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/ws`;

    console.log('🔌 Connecting WebSocket:', {
      socketUrl,
      firebaseUid,
      databaseUserId,
      envApiBase: import.meta.env.VITE_API_BASE_URL
    });

    this.client = new Client({
      webSocketFactory: () => {
        console.log('🏭 Creating SockJS instance for:', socketUrl);
        return new SockJS(socketUrl) as any;
      },
      connectHeaders: {
        userId: firebaseUid, // Send Firebase UID to backend for auth
      },
      debug: (str: string) => {
        console.log('📡 STOMP:', str);
      },
      reconnectDelay: 0, // Disable automatic reconnection
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
    });

    this.client.onConnect = () => {
      console.log('WebSocket connected successfully');
      this.connected = true;
      this.connecting = false;
      this.reconnectAttempts = 0;
      
      // Clear any pending reconnect timeout
      if (this.reconnectTimeout) {
        clearTimeout(this.reconnectTimeout);
        this.reconnectTimeout = null;
      }

      // Subscribe to user-specific notifications
      // When using convertAndSendToUser, Spring automatically routes to the user's session
      // So we subscribe to /user/queue/notifications (Spring adds the user prefix internally)
      const subscriptionPath = `/user/queue/notifications`;
      console.log('Subscribing to:', subscriptionPath);
      console.log('(Spring will route this to the authenticated user session)');
      
      this.client?.subscribe(subscriptionPath, (message: any) => {
        console.log('✨ Raw message received:', message);
        const notification: GamificationNotification = JSON.parse(message.body);
        console.log('✨ Parsed notification:', notification);
        this.callbacks.forEach((callback) => callback(notification));
      });
      
      console.log('WebSocket subscription registered');
    };

    this.client.onStompError = (frame: any) => {
      console.error('STOMP error:', frame);
      this.connected = false;
      this.connecting = false;
      this.attemptReconnect();
    };

    this.client.onWebSocketClose = () => {
      console.log('WebSocket connection closed');
      this.connected = false;
      this.connecting = false;
      this.attemptReconnect();
    };

    this.client.onWebSocketError = (error: any) => {
      console.error('WebSocket error:', error);
      this.connected = false;
      this.connecting = false;
    };

    try {
      this.client.activate();
    } catch (error) {
      console.error('Failed to activate WebSocket:', error);
      this.connecting = false;
    }
  }

  private attemptReconnect() {
    // Clear any existing timeout
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }

    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      const delay = this.reconnectDelay * this.reconnectAttempts; // Exponential backoff
      console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts}) in ${delay}ms...`);
      
      this.reconnectTimeout = setTimeout(() => {
        if (this.currentUserId && this.currentDatabaseId) {
          this.connect(this.currentUserId, this.currentDatabaseId);
        }
      }, delay);
    } else {
      console.error('Max reconnection attempts reached. Please refresh the page.');
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
      this.connected = false;
      console.log('WebSocket disconnected');
    }
  }

  onNotification(callback: NotificationCallback) {
    this.callbacks.push(callback);
  }

  removeNotificationListener(callback: NotificationCallback) {
    this.callbacks = this.callbacks.filter((cb) => cb !== callback);
  }

  isConnected(): boolean {
    return this.connected;
  }
}

export const webSocketService = new WebSocketService();
