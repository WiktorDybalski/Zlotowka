"use client";

import RegistrationForm from "@/components/login_pages/RegisterForm";
import routes from "@/routes";
import { redirect } from "next/navigation";

interface FormData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export default function RegisterPage() {
  const handleFormSubmit = (formData: Record<string, string>) => {
    formData as unknown as FormData; //for type checking, TS do not kill me
    console.log("Submitted data:", formData);
    alert("Submitted data:" + JSON.stringify(formData));
    redirect(routes.dashboard.pathname);
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
    />
  );
}
