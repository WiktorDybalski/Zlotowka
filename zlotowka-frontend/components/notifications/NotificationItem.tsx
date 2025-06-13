import React from "react";
import { AppNotificationDTO } from "@/interfaces/notifications/Notifications";

export interface NotificationItemProps {
    notification: AppNotificationDTO;
    onMarkRead: (id: number) => void;
}

export const NotificationItem: React.FC<NotificationItemProps> = ({
                                                                      notification,
                                                                      onMarkRead,
                                                                  }) => {
    const { id, category, text, createdAt } = notification;
    return (
        <div key={id} className="bg-gray-100 rounded-lg p-4 mb-4">
            <div className="flex justify-between">
                <h3 className="text-lg font-medium text-[#2a9d8f]">{category}</h3>
                <button
                    onClick={() => onMarkRead(id)}
                    aria-label="Oznacz jako przeczytane"
                    className="text-neutral-500 hover:text-neutral-700"
                >
                    <span className="material-symbols-outlined text-xl">x</span>
                </button>
            </div>
            <p className="text-gray-700">{text}</p>
            <p className="text-xs text-gray-500 mt-1">
                {new Date(createdAt).toLocaleString()}
            </p>
        </div>
    );
};
