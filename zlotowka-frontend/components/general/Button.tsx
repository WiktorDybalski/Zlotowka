"use client";

import clsx from "clsx";

interface ButtonProps {
  onClick?: () => void;
  className?: string;
  children?: React.ReactNode;
  type?: "submit" | "button";
  icon?: string;
  variant?: "dark" | "accent";
  disabled?: boolean;
}

export default function Button({
  onClick,
  className,
  children,
  type = "button",
  icon,
  variant = "accent",
  disabled = false,
}: ButtonProps) {
  return (
    <button
      className={clsx(
        className,
        "flex items-center justify-center gap-x-2 px-8 transition duration-200 ease-in-out",
        disabled ? "opacity-50 cursor-not-allowed" : "hover:cursor-pointer",
        variant === "accent" &&
          "bg-accent hover:bg-backgroundDark text-background py-4 text-lg rounded-lg",
        variant === "dark" &&
          "bg-accent hover:bg-[#141414] text-background w-full py-2 text-sm rounded-[10px]"
      )}
      onClick={onClick}
      type={type}
      disabled={disabled}
    >
      {icon && <span className="material-symbols">{icon}</span>}
      {children && <h3>{children}</h3>}
    </button>
  );
}
