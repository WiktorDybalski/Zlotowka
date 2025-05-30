"use client";
import { useState } from "react";
import Hstack from "@/components/general/Hstack";
import LightLink from "@/components/general/LightLink";
import FormInput from "@/components/login_pages/FormInput";
import RegisterButton from "@/components/general/Button";

export interface FormInputData {
  id: "firstName" | "lastName" | "email" | "password" | string;
  type?: "password" | "email" | "text";
  placeholder: string;
  inputLeftElement?: React.ReactNode;
  defaultValue?: string;
  readOnly?: boolean;
}

interface LinkData {
  href: string;
  text: string;
}

interface RegistrationFormProps {
  onSubmit?: (formData: Record<string, string>) => void;
  title?: string;
  inputs: FormInputData[]; // List of data for FormInput
  registerButtonText: string;
  links?: LinkData[]; // List of links to display
  disabled?: boolean; // Optional prop to disable the button
}

const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

export default function RegistrationForm({
  onSubmit,
  title,
  inputs,
  registerButtonText,
  links,
  disabled = false,
}: RegistrationFormProps) {
  const [formData, setFormData] = useState<Record<string, string>>({});
  const [errors, setErrors] = useState<Record<string, string>>({});

  const handleChange = (id: string, value: string) => {
    setFormData((prev) => ({ ...prev, [id]: value }));
    setErrors((prev) => ({ ...prev, [id]: "" })); // Reset error when value changes
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const newErrors: Record<string, string> = {};

    // Data validation
    inputs.forEach((input) => {
      if (input.readOnly){
        return;
      }
      if (!formData[input.id] || formData[input.id].trim() === "") {
        newErrors[input.id] = "To pole jest wymagane.";
      } else if (input.id === "email" && !emailRegex.test(formData[input.id])) {
        newErrors[input.id] = "Podaj poprawny adres email.";
      } else if (input.id === "password" && formData[input.id].length < 6) {
        newErrors[input.id] = "Hasło musi mieć co najmniej 6 znaków.";
      }
    });

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    if (onSubmit) {
      onSubmit(formData); // Pass data to the onSubmit function
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="w-full max-w-md p-8 px-13 space-y-8 bg-white rounded-lg shadow-lg border-lightAccent border-2"
    >
      {/* Header h2 */}
      {title && (
        <div className="text-center py-3">
          <h2 className="text-4xl font-extrabold text-[#262626]">{title}</h2>
        </div>
      )}

      <Hstack className="space-y-6 mt-5">
        {/* Generate FormInput fields based on provided data */}
        {inputs.map((input) => (
          <FormInput
            key={input.id}
            id={input.id}
            type={input.type}
            placeholder={input.placeholder}
            inputLeftElement={input.inputLeftElement}
            defaultValue={input.defaultValue}
            readOnly={input.readOnly}
            className="w-full"
            onChange={(e) => handleChange(input.id, e.target.value)}
            errorMessage={errors[input.id]}
          />
        ))}

        <RegisterButton variant="accent" type="submit" disabled={disabled}>
          {registerButtonText}
        </RegisterButton>

        {/* Links */}
        {links &&
          links.map((link, index) => (
            <LightLink key={index} href={link.href} disabled={disabled}>
              {link.text}
            </LightLink>
          ))}
      </Hstack>
    </form>
  );
}
