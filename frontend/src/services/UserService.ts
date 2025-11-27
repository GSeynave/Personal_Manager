import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const API_URL = `/api/users`;

export interface UserIdentity {
  userTag?: string;
  email?: string;
  // Add other identity fields as needed
}

export default class UserService {

  async getUserIdentity(): Promise<UserIdentity | null> {
    try {
      // Use /me endpoint - backend extracts userId from JWT token
      const response = await axios.get(`${API_URL}/me`);
      return response.data;
    } catch (error) {
      console.error('Error fetching user identity:', error);
      return null;
    }
  }

  async updateUserTag(usertag: string) {
    const authStore = useAuthStore();
    const userEmail = authStore.userEmail;
    
    console.log(`Updating user tag to ${usertag}`);
    // Use /me endpoint - backend extracts userId from JWT token
    const response = await axios.put(`${API_URL}/me`, { 
      email: userEmail,
      userTag: usertag 
    });
    return response.data;
  }

}
