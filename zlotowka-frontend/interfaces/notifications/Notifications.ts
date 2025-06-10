export interface AppNotificationDTO {
    id: number;
    category: string;
    text: string;
    createdAt: string;
    byEmail: boolean;
    byPhone: boolean;
}
