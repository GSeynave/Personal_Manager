export default class UserIdentity {
    id: string;
    username: string;
    email: string | null = null;


    constructor(id: string, username: string, email: string | null) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}