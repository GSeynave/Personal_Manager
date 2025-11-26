import { initializeApp, type FirebaseApp } from "firebase/app";
import { getAnalytics, type Analytics } from "firebase/analytics";
import { 
  getAuth, 
  type Auth, 
  type User,
  type UserCredential,
  createUserWithEmailAndPassword, 
  signInWithEmailAndPassword, 
  onAuthStateChanged, 
  signOut,
  type Unsubscribe
} from "firebase/auth";

// Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyBoHhxnCTKhvfMfuErHFTKHKBQ_w_rArqI",
  authDomain: "personal-manager-gse.firebaseapp.com",
  projectId: "personal-manager-gse",
  storageBucket: "personal-manager-gse.firebasestorage.app",
  messagingSenderId: "642743087678",
  appId: "1:642743087678:web:eb1a8e6e8f15cb8c7a176f",
  measurementId: "G-QYSJGVXE1L"
};

interface AuthResult {
  success: boolean;
  user?: User;
  error?: string;
  code?: string;
}

class FirebaseService {
  private static instance: FirebaseService;
  private app: FirebaseApp | null = null;
  private analytics: Analytics | null = null;
  private auth: Auth | null = null;
  private currentUser: User | null = null;

  private constructor() {}

  public static getInstance(): FirebaseService {
    if (!FirebaseService.instance) {
      FirebaseService.instance = new FirebaseService();
    }
    return FirebaseService.instance;
  }

  public initFirebase(): FirebaseApp {
    if (!this.app) {
      this.app = initializeApp(firebaseConfig);
      this.analytics = getAnalytics(this.app);
      this.auth = getAuth(this.app);
      
      // Set up auth state listener
      onAuthStateChanged(this.auth, (user) => {
        this.currentUser = user;
      });
    }
    return this.app;
  }

  public getAuth(): Auth {
    if (!this.auth) {
      this.initFirebase();
    }
    return this.auth!;
  }

  public isUserAuthenticated(): boolean {
    return this.currentUser !== null;
  }

  public getCurrentUser(): User | null {
    return this.currentUser;
  }

  public async signUp(email: string, password: string): Promise<AuthResult> {
    try {
      // Ensure Firebase is fully initialized
      this.initFirebase();
      
      if (!this.auth) {
        throw new Error("Firebase Auth not initialized");
      }
      
      const userCredential: UserCredential = await createUserWithEmailAndPassword(
        this.auth,
        email,
        password
      );
      return { success: true, user: userCredential.user };
    } catch (error: any) {
      console.error("Sign up error:", error.code, error.message);
      return { success: false, error: error.message, code: error.code };
    }
  }

  public async signIn(email: string, password: string): Promise<AuthResult> {
    try {
      // Ensure Firebase is fully initialized
      this.initFirebase();
      
      if (!this.auth) {
        throw new Error("Firebase Auth not initialized");
      }
      
      const userCredential: UserCredential = await signInWithEmailAndPassword(
        this.auth,
        email,
        password
      );
      return { success: true, user: userCredential.user };
    } catch (error: any) {
      console.error("Sign in error:", error.code, error.message);
      return { success: false, error: error.message, code: error.code };
    }
  }

  public async signOutUser(): Promise<{ success: boolean; error?: string }> {
    try {
      if (!this.auth) {
        throw new Error("Auth not initialized");
      }
      await signOut(this.auth);
      return { success: true };
    } catch (error: any) {
      console.error("Sign out error:", error.code, error.message);
      return { success: false, error: error.message };
    }
  }

  public onAuthStateChange(callback: (user: User | null) => void): Unsubscribe {
    if (!this.auth) {
      this.initFirebase();
    }
    return onAuthStateChanged(this.auth!, callback);
  }
}

export default FirebaseService.getInstance();