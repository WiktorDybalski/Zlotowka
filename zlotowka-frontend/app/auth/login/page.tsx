"use client";

import LoginForm from "@/components/login_pages/RegisterForm";
import routes from "@/routes";

interface FormData {
  email: string;
  password: string;
}

export default function LoginPage() {
  const handleFormSubmit = (formData: Record<string, string>) => {
    formData as unknown as FormData; //for type checking, TS do not kill me
    console.log("Submitted data:", formData);
    alert("Submitted data:" + JSON.stringify(formData));
  };

  return (
    <LoginForm
      inputs={[
        {
          id: "email",
          placeholder: "Email",
          inputLeftElement: "@",
        },
        {
          id: "password",
          type: "password",
          placeholder: "Hasło",
          inputLeftElement: "#",
        },
      ]}
      links={[
        { href: routes.register.pathname, text: "Nie masz konta? Utwórz je!" },
        { href: routes.forgotPassword.pathname, text: "Zapomniałeś hasła?" }, //TODO: change link in the future
      ]}
      registerButtonText="Zaloguj się!"
      onSubmit={handleFormSubmit}
    />
  );
}
