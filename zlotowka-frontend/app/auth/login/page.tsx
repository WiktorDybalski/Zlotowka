"use client";

import LoginForm from "@/components/login_pages/RegisterForm";
import routes from "@/routes";
import { useLoginService } from "@/services/LoginService";
import { useMutation } from "@tanstack/react-query";
import toast from "react-hot-toast";
// import { redirect } from "next/navigation";

interface FormData {
  email: string;
  password: string;
}

export default function LoginPage() {
  const LoginService = useLoginService();

  const magic = useMutation({
    mutationFn: async (data: FormData) => {
      const { email, password } = data;
      const loginPromise = LoginService.loginUser({ email, password });
      toast.promise(loginPromise, {
        loading: "Logowanie...",
        success: "Zalogowano pomyślnie!",
        error: (error) => `Wystąpił błąd podczas logowania: ${error.message}`,
      });
      return await loginPromise;
    },
    onSuccess: () => {
      // redirect(routes.dashboard.pathname); //auto redirect, auto login
    },
  });

  const handleFormSubmit = (formData: Record<string, string>) => {
    formData as unknown as FormData; //for type checking, TS do not kill me
    const { email, password } = formData;
    magic.mutate({ email, password });
  };

  return (
    <LoginForm
      title="Zaloguj się"
      inputs={[
        { id: "email", placeholder: "Email" },
        { id: "password", type: "password", placeholder: "Hasło" },
      ]}
      links={[
        { href: routes.register.pathname, text: "Nie masz konta? Utwórz je!" },
        { href: routes.forgotPassword.pathname, text: "Zapomniałeś hasła?" },
      ]}
      registerButtonText="Zaloguj się!"
      onSubmit={handleFormSubmit}
      disabled={magic.isPending}
    />
  );
}
