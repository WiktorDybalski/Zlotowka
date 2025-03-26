import clsx from "clsx";

interface FormInputProps {
  id: string;
  type?: string;
  placeholder?: string;
  label?: string;
  className?: string; // Dodatkowe klasy
  inputLeftElement?: React.ReactNode; // Element po lewej stronie inputa
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  required?: boolean;
}

export default function FormInput({
  id,
  type = "text",
  placeholder = "",
  label,
  className,
  inputLeftElement,
  onChange,
  required = true,
}: FormInputProps) {
  return (
    <div className="flex flex-col space-y-2 w-full">
      {label && (
        <label htmlFor={id} className="text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <div className="relative flex items-center">
        {inputLeftElement && (
          <div className="absolute left-3 text-gray-500">
            {inputLeftElement}
          </div>
        )}
        <input
          id={id}
          type={type}
          placeholder={placeholder}
          onChange={onChange}
          required={required}
          className={clsx(
            `w-full border border-lightAccent text-backgroundLightDark rounded-md ${
              inputLeftElement ? "pl-10" : ""
            } py-2 px-3 focus:outline-none focus:ring-2 focus:ring-accent focus:border-accent`,
            className
          )}
        />
      </div>
    </div>
  );
}
