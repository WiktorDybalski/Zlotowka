"use client";

import { useRouter, useSearchParams } from "next/navigation";
import routes from "@/routes";
import { usePasswordService, ResetResponse } from "@/services/PasswordService";
import { useMutation } from "@tanstack/react-query";
import toast from "react-hot-toast";
import RegisterForm from "@/components/login_pages/RegisterForm";
import type { FormInputData } from "@/components/login_pages/RegisterForm";

export default function ResetPasswordPage() {
    const router = useRouter();
    const params = useSearchParams();
    const defaultEmail = params.get("email") ?? "";

    const { resetPassword } = usePasswordService();

    const mutation = useMutation<
        ResetResponse,
        Error,
        { email: string; token: string; newPassword: string }
    >({
        mutationFn: ({ email, token, newPassword }) =>
            resetPassword(email, token, newPassword),
        onSuccess: () => {
            toast.success("Hasło zostało zmienione pomyślnie");
            router.push(routes.login.pathname);
        },
        onError: () => {
            toast.error(
                "Nie udało się zmienić hasła. Sprawdź token i spróbuj ponownie."
            );
        },
    });

    const handleFormSubmit = (formData: Record<string, string>) => {
        mutation.mutate({
            email: formData.email || defaultEmail,
            token: formData.token,
            newPassword: formData.newPassword,
        });
    };

    const inputs: FormInputData[] = [
        {
            id: "email",
            placeholder: "Email",
            type: "email",
            defaultValue: defaultEmail,
            readOnly: true,
        },
        {
            id: "token",
            placeholder: "Kod resetujący",
            type: "text",
        },
        {
            id: "newPassword",
            placeholder: "Nowe hasło",
            type: "password",
        },
    ];

    return (
        <RegisterForm
            title="Ustaw nowe hasło"
            inputs={inputs}
            registerButtonText={mutation.isPending ? "Zmiana..." : "Zmień hasło"}
            links={[{ href: routes.login.pathname, text: "Powrót do logowania" }]}
            onSubmit={handleFormSubmit}
            disabled={mutation.isPending}
        />
    );
}
