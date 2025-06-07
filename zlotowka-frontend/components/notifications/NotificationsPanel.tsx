"use client";

import React, { useEffect, useState } from "react";
import { createPortal } from "react-dom";
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

    const [mounted, setMounted] = useState(false);

    const { fetchNotifications, markAsRead } = useNotificationService();

    const [notifications, setNotifications] = useState<AppNotificationDTO[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        setMounted(true);
    }, []);

    useEffect(() => {
        if (!mounted || !token) return;
        setLoading(true);
        setError(null);

        fetchNotifications(token)
            .then((data) => {
                setNotifications(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Błąd podczas pobierania notyfikacji:", err);
                setError("Nie można pobrać powiadomień");
                setLoading(false);
            });
    }, [mounted, token]);

    const handleMarkAsRead = (id: number) => {
        if (!token) return;
        markAsRead(id, token)
            .then(() => {
                setNotifications((prev) => prev.filter((n) => n.id !== id));
            })
            .catch((err) => {
                console.error("Błąd podczas oznaczania jako przeczytane:", err);
                alert("Nie udało się oznaczyć powiadomienia jako przeczytane");
            });
    };

    if (!mounted) return null;

    let notificationsRoot = document.getElementById("notifications-root");
    if (!notificationsRoot) {
        notificationsRoot = document.createElement("div");
        notificationsRoot.setAttribute("id", "notifications-root");
        document.body.appendChild(notificationsRoot);
    }

    const panelContent = (
        <div className="fixed inset-0 z-[9999]">
            <div
                className="absolute inset-0 bg-black/40 z-[9999]"
                onClick={onClose}
            />

            <div
                className="
          absolute top-0 left-0 ml-[250px]
          w-[33vw] max-w-[400px] h-full bg-white shadow-lg
          flex flex-col overflow-y-auto z-[10000]
        "
            >
                <div className="flex justify-end p-4 z-[10001]">
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

                <div className="px-6 py-4 flex-1">
                    {loading ? (
                        <p>Ładowanie...</p>
                    ) : error ? (
                        <p className="text-red-600">{error}</p>
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
                                        onClick={() => handleMarkAsRead(notif.id)}
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

    return createPortal(panelContent, notificationsRoot);
}
