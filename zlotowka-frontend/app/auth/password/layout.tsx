"use client";

import { RedirectWhenLogged } from "@/components/providers/AuthProvider";

export default function PasswordLayout({
                                           children,
                                       }: {
    children: React.ReactNode;
}) {
    return (
        <RedirectWhenLogged>
            <main className="min-h-screen bg-white flex items-center justify-center px-4">
                {children}
            </main>
        </RedirectWhenLogged>
    );
}
