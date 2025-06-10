import React from "react";
import { AppNotificationDTO } from "@/interfaces/notifications/Notifications";
import { NotificationItem } from "./NotificationItem";

export interface NotificationsListProps {
    notifications: AppNotificationDTO[];
    isLoading: boolean;
    isError: boolean;
    onMarkRead: (id: number) => void;
}

export const NotificationsList: React.FC<NotificationsListProps> = ({
                                                                        notifications,
                                                                        isLoading,
                                                                        isError,
                                                                        onMarkRead,
                                                                    }) => {
    if (isLoading) return <p>Ładowanie...</p>;
    if (isError) return <p className="text-red-600">Nie można pobrać powiadomień</p>;
    if (notifications.length === 0) return <p>Brak nowych powiadomień.</p>;

    return (
        <>
            {notifications.map((notif) => (
                <NotificationItem
                    key={notif.id}
                    notification={notif}
                    onMarkRead={onMarkRead}
                />
            ))}
        </>
    );
};
