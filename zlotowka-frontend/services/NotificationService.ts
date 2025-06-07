export interface AppNotificationDTO {
    id: number;
    category: string;
    text: string;
    createdAt: string;
    byEmail: boolean;
    byPhone: boolean;
}

export function useNotificationService() {
    async function fetchNotifications(token: string): Promise<AppNotificationDTO[]> {
        const response = await fetch("http://localhost:8080/notifications", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });
        if (!response.ok) {
            throw new Error("Nie można pobrać powiadomień");
        }
        return await response.json();
    }

    async function markAsRead(notificationId: number, token: string) {
        const response = await fetch(
            `http://localhost:8080/notifications/${notificationId}`,
            {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            }
        );
        if (!response.ok) {
            throw new Error("Nie udało się oznaczyć powiadomienia jako przeczytane");
        }
    }

    return { fetchNotifications, markAsRead };
}
