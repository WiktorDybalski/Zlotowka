"use client";

import RegistrationForm from "@/components/login_pages/RegistrationForm";

interface FormData {
  username: string;
  email: string;
  password: string;
}

export default function RegisterPage() {
  const handleFormSubmit = (formData: Record<string, string>) => {
    formData as unknown as FormData; //for type checking, TS do not kill me
    console.log("Submitted data:", formData);
    alert("Submitted data:" + JSON.stringify(formData));
  };

  return (
    <RegistrationForm
      title="Zarejestruj się"
      inputs={[
        { id: "username", placeholder: "Nazwa użytkownika" },
        {
          id: "email",
          type: "email",
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
      links={[{ href: "/login", text: "Masz już konto? Zaloguj się!" }]}
      registerButtonText="Zarejestruj się!"
      onSubmit={handleFormSubmit}
    />
  );
}
