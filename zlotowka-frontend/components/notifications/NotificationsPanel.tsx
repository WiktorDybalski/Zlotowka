"use client";

import React from "react";
import { createPortal } from "react-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "@/components/providers/AuthProvider";
import {
    AppNotificationDTO,
    useNotificationService,
} from "@/services/NotificationService";

interface NotificationsPanelProps {
    onClose: () => void;
}

export default function NotificationsPanel({ onClose }: NotificationsPanelProps) {
    const { token } = useAuth();
    const { fetchNotifications, markAsRead } = useNotificationService();
    const queryClient = useQueryClient();

    const notificationsQuery = useQuery<AppNotificationDTO[], Error>({
        queryKey: ["notifications"],
        queryFn: () => fetchNotifications(token!),
        enabled: !!token,
    });

    const { data: notifications = [], isLoading, isError } = notificationsQuery;

    const markReadMutation = useMutation<void, Error, number>({
        mutationFn: (id) => markAsRead(id, token!),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["notifications"] });
        },
        onError: () => {
            alert("Nie udało się oznaczyć powiadomienia jako przeczytane");
        },
    });

    if (!token) return null;

    const panel = (
        <div className="fixed inset-0 z-50">
            <div
                className="absolute inset-0 bg-black/40"
                onClick={onClose}
            />

            <div
                className="
          absolute top-0 left-0
          ml-[250px]
          w-[33vw] max-w-[400px] h-full
          bg-white
          shadow-lg
          flex flex-col overflow-y-auto
          z-50
        "
            >
                <div className="flex justify-end p-4">
                    <button
                        onClick={onClose}
                        className="text-neutral-600 hover:text-neutral-800 transition-colors"
                        aria-label="Zamknij panel powiadomień"
                    >
            <span className="material-symbols-outlined text-2xl">
              close
            </span>
                    </button>
                </div>

                <h2 className="px-6 text-2xl font-semibold mb-4">Powiadomienia</h2>
                <hr />

                <div className="px-6 py-4 flex-1 bg-white">
                    {isLoading ? (
                        <p>Ładowanie...</p>
                    ) : isError ? (
                        <p className="text-red-600">Nie można pobrać powiadomień</p>
                    ) : notifications.length === 0 ? (
                        <p>Brak nowych powiadomień.</p>
                    ) : (
                        notifications.map((notif) => (
                            <div
                                key={notif.id}
                                className="bg-gray-100 rounded-lg p-4 mb-4"
                            >
                                <div className="flex justify-between">
                                    <h3 className="text-lg font-medium text-[#2a9d8f]">
                                        {notif.category}
                                    </h3>
                                    <button
                                        onClick={() => markReadMutation.mutate(notif.id)}
                                        className="text-neutral-500 hover:text-neutral-700"
                                        aria-label="Oznacz jako przeczytane"
                                    >
                    <span className="material-symbols-outlined text-xl">
                      done
                    </span>
                                    </button>
                                </div>
                                <p className="text-gray-700">{notif.text}</p>
                                <p className="text-xs text-gray-500 mt-1">
                                    {new Date(notif.createdAt).toLocaleString()}
                                </p>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );

    const root = document.getElementById("notifications-root")!;
    return createPortal(panel, root);
}
