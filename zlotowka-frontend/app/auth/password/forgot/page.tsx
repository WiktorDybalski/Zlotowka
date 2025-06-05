"use client";

import { useRouter } from "next/navigation";
import routes from "@/routes";
import { usePasswordService, ForgotResponse } from "@/services/PasswordService";
import { useMutation } from "@tanstack/react-query";
import toast from "react-hot-toast";
import RegisterForm from "@/components/login_pages/RegisterForm";

export default function ForgotPasswordPage() {
    const router = useRouter();
    const { forgotPassword } = usePasswordService();

    const mutation = useMutation<ForgotResponse, Error, string>({
        mutationFn: (email) => forgotPassword(email),
        onSuccess: (_data, email) => {
            toast.success("Kod został wysłany na Twój email");
            router.push(
                `${routes.forgotPassword.pathname.replace(
                    "forgot",
                    "reset"
                )}?email=${encodeURIComponent(email)}`
            );
        },
        onError: () => {
            toast.error("Nie udało się wysłać kodu. Spróbuj ponownie.");
        },
    });

    const handleFormSubmit = (formData: Record<string, string>) => {
        mutation.mutate(formData.email);
    };

    return (
        <RegisterForm
            title="Przypomnij hasło"
            inputs={[{ id: "email", placeholder: "Email" }]}
            registerButtonText={
                mutation.isPending ? "Wysyłam..." : "Wyślij kod"
            }
            links={[{ href: routes.login.pathname, text: "Powrót do logowania" }]}
            onSubmit={handleFormSubmit}
            disabled={mutation.isPending}
        />
    );
}
