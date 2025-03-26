"use client";
import { useState } from "react";
import RegisterButton from "@/components/general/ButtonWithBackground";
import Hstack from "@/components/general/Hstack";
import LightLink from "@/components/general/LightLink";
import FormInput from "@/components/login_pages/FormInput";

interface FormInputData {
  id: string;
  type?: string;
  placeholder: string;
  inputLeftElement?: React.ReactNode;
}

interface LinkData {
  href: string;
  text: string;
}

interface RegistrationFormProps {
  onSubmit?: (formData: Record<string, string>) => void;
  title?: string;
  inputs: FormInputData[]; // Lista danych dla FormInput
  registerButtonText: string;
  links?: LinkData[]; // Lista linków do wyświetlenia
}

export default function RegistrationForm({
  onSubmit,
  title,
  inputs,
  registerButtonText,
  links,
}: RegistrationFormProps) {
  const [formData, setFormData] = useState<Record<string, string>>({});

  const handleChange = (id: string, value: string) => {
    setFormData((prev) => ({ ...prev, [id]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (onSubmit) {
      onSubmit(formData); // Przekazanie danych do funkcji onSubmit
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="w-full max-w-md p-8 px-13 space-y-8 bg-white rounded-lg shadow-lg border-lightAccent border-2"
    >
      {/* Nagłówek h2 */}
      {title && (
        <div className="text-center py-3">
          <h2 className="text-4xl font-extrabold text-[#262626]">{title}</h2>
        </div>
      )}

      <Hstack className="space-y-6 mt-5">
        {/* Generowanie pól FormInput na podstawie przekazanych danych */}
        {inputs.map((input) => (
          <FormInput
            key={input.id}
            id={input.id}
            type={input.type}
            placeholder={input.placeholder}
            inputLeftElement={input.inputLeftElement}
            className="w-full"
            onChange={(e) => handleChange(input.id, e.target.value)}
          />
        ))}

        {/* Przycisk rejestracji */}
        <RegisterButton className="font-semibold mt-4" type="submit">
          {registerButtonText}
        </RegisterButton>

        {/* Linki */}
        {links &&
          links.map((link, index) => (
            <LightLink key={index} href={link.href}>
              {link.text}
            </LightLink>
          ))}
      </Hstack>
    </form>
  );
}
