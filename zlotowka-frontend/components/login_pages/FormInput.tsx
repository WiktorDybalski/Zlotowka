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
  errorMessage?: string; // Jeśli jest zapala na czerwono
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
  errorMessage,
}: FormInputProps) {
  return (
    (label = errorMessage ? errorMessage : label), //podmiana label na errorMessage
    (
      <div className="flex flex-col space-y-2 w-full">
        {label && (
          <label
            htmlFor={id}
            className={clsx(
              "text-sm font-medium",
              errorMessage ? "text-error" : "text-gray-700", // Zmiana koloru w zależności od błędu
            )}
          >
            {label}
          </label>
        )}
        <div className="relative flex items-center">
          {inputLeftElement && (
            <div
              className={clsx(
                "absolute left-3 flex items-center h-full", // Wyśrodkowanie elementu
                errorMessage ? "text-error" : "text-gray-500", // Zmiana koloru w zależności od błędu
              )}
            >
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
              "w-full border rounded-md py-2 px-3 focus:outline-none focus:ring-2",
              inputLeftElement ? "pl-10" : "",
              errorMessage
                ? "border-error focus:ring-error"
                : "border-lightAccent focus:ring-accent",
              className,
            )}
          />
        </div>
      </div>
    )
  );
}
