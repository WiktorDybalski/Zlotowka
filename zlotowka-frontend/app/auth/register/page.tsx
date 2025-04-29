"use client";

import RegistrationForm from "@/components/login_pages/RegisterForm";
import routes from "@/routes";
import { useLoginService } from "@/services/LoginService";
import { useMutation } from "@tanstack/react-query";
import toast from "react-hot-toast";
// import { redirect } from "next/navigation";

interface FormData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export default function RegisterPage() {
  const LoginService = useLoginService();

  const magic = useMutation({
    mutationFn: async (data: FormData) => {
      const { firstName, lastName, email, password } = data;
      const registerPromise = LoginService.registerUser({
        firstName,
        lastName,
        email,
        password,
      });
      toast.promise(registerPromise, {
        loading: "Rejestracja...",
        success: "Zarejestrowano pomyślnie!",
        error: (error) => `Wystąpił błąd podczas rejestracji: ${error.message}`,
      });
      return await registerPromise;
    },
    onSuccess: () => {
      // redirect(routes.dashboard.pathname); //auto redirect, auto login
    },
  });

  const handleFormSubmit = (formData: Record<string, string>) => {
    formData as unknown as FormData; //for type checking, TS do not kill me
    const { firstName, lastName, email, password } = formData;

    magic.mutate({ firstName, lastName, email, password });
  };

  return (
    <RegistrationForm
      title="Zarejestruj się"
      inputs={[
        { id: "firstName", placeholder: "Imię" },
        { id: "lastName", placeholder: "Nazwisko" },
        { id: "email", placeholder: "Email" },
        { id: "password", type: "password", placeholder: "Hasło" },
      ]}
      links={[
        { href: routes.login.pathname, text: "Masz już konto? Zaloguj się!" },
      ]}
      registerButtonText="Zarejestruj się!"
      onSubmit={handleFormSubmit}
      disabled={magic.isPending}
    />
  );
}
