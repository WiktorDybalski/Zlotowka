"use client";

import LoginForm from "@/components/login_pages/RegisterForm";
import { useAuth } from "@/components/providers/LoginProvider";
import routes from "@/routes";

interface FormData {
  email: string;
  password: string;
}

export default function LoginPage() {
  const Login = useAuth();

  const handleFormSubmit = (formData: Record<string, string>) => {
    formData as unknown as FormData; //for type checking, TS do not kill me
    console.log("Submitted data:", formData);
    Login.setLogin("fajny_token"); // TODO: !!!!!!!!!! replace with real token from backend
    alert("Submitted data:" + JSON.stringify(formData));
  };

  return (
    <LoginForm
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
    />
  );
}
