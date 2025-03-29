"use client";

import clsx from "clsx";

interface ButtonProps {
  onClick?: () => void;
  className?: string;
  children?: React.ReactNode;
  type?: "submit" | "button";
  icon?: string;
  variant?: "dark" | "accent";
}

export default function Button({
  onClick,
  className,
  children,
  type = "button",
  icon,
  variant = "accent",
}: ButtonProps) {
  return (
    <button
      className={clsx(
        className,
        "flex items-center justify-center gap-x-2 px-8 transition duration-200 ease-in-out hover:cursor-pointer",
        variant === "accent" &&
          "bg-accent hover:bg-backgroundDark text-background py-4 text-lg rounded-lg ",
        variant === "dark" &&
          "bg-[#262626] hover:bg-[#141414] text-neutral-100 w-full py-2 text-sm rounded-[10px]"
      )}
      onClick={onClick}
      type={type}
    >
      {icon && <span className="material-symbols">{icon}</span>}
      {children && <h3>{children}</h3>}
    </button>
  );
}
