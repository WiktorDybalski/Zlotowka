import React from "react";
import { createPortal } from "react-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "@/components/providers/AuthProvider";
import { useNotificationService } from "@/services/NotificationService";
import { AppNotificationDTO } from "@/interfaces/notifications/Notifications";
import { NotificationsList } from "./NotificationsList";
import { toast } from 'react-hot-toast';

interface NotificationsPanelProps {
    onClose: () => void;
}

export default function NotificationsPanel({ onClose }: NotificationsPanelProps) {
    const { token } = useAuth();
    const { fetchNotifications, markAsRead } = useNotificationService();
    const queryClient = useQueryClient();

    const { data: notifications = [], isLoading, isError } = useQuery<
        AppNotificationDTO[],
        Error
    >({
        queryKey: ["notifications"],
        queryFn: fetchNotifications,
        enabled: !!token,
    });

    const markRead = useMutation<void, Error, number>({
        mutationFn: markAsRead,
        onSuccess: () => queryClient.invalidateQueries({ queryKey: ["notifications"] }),
        onError: () => toast.error("Nie udało się oznaczyć powiadomienia jako przeczytane"),
    });

    if (!token) return null;

    const panel = (
        <div className="fixed inset-0 z-50">
            <div className="absolute inset-0 bg-black/40" onClick={onClose} />
            <div className="absolute top-0 left-0 ml-[250px] w-[33vw] max-w-[400px] h-full bg-white shadow-lg flex flex-col overflow-y-auto z-50">
                <div className="flex justify-end p-4">
                    <button onClick={onClose} aria-label="Zamknij panel powiadomień" className="text-neutral-600 hover:text-neutral-800">
                        <span className="material-symbols-outlined text-2xl">x</span>
                    </button>
                </div>
                <h2 className="px-6 text-2xl font-semibold mb-4">Powiadomienia</h2>
                <hr />
                <div className="px-6 py-4 flex-1">
                    <NotificationsList
                        notifications={notifications}
                        isLoading={isLoading}
                        isError={isError}
                        onMarkRead={(id) => markRead.mutate(id)}
                    />
                </div>
            </div>
        </div>
    );

    const root = document.getElementById("notifications-root");
    return root ? createPortal(panel, root) : null;
}
