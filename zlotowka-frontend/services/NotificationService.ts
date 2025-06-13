import sendToBackend, { sendToBackendWithoutReturningJson, getAuthHeader } from "@/lib/sendToBackend";
import { AppNotificationDTO } from "@/interfaces/notifications/Notifications";
import { useAuth } from "@/components/providers/AuthProvider";

export function useNotificationService() {
    const { token } = useAuth();
    if (!token) throw new Error("User not authenticated");
    const authHeader = getAuthHeader(token);

    async function fetchNotifications(): Promise<AppNotificationDTO[]> {
        return await sendToBackend(
            "notifications",
            { ...authHeader, method: "GET" },
            "Nie można pobrać powiadomień"
        );
    }

    async function markAsRead(notificationId: number): Promise<void> {
        await sendToBackendWithoutReturningJson(
            `notifications/${notificationId}`,
            { ...authHeader, method: "DELETE" },
            "Nie udało się oznaczyć powiadomienia jako przeczytane"
        );
    }

    return { fetchNotifications, markAsRead };
}
