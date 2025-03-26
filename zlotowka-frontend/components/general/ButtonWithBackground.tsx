"use client";

import { Button } from "@/components/ui/button";
import clsx from "clsx";

interface RegisterButtonProps {
  onClick?: () => void;
  className?: string;
  children?: React.ReactNode;
  type?: "submit" | "button";
}

export default function RegisterButton({
  onClick,
  className,
  children,
  type,
}: RegisterButtonProps) {
  return (
    <Button
      className={clsx(
        "bg-accent hover:bg-backgroundDark text-background px-8 py-6 text-lg rounded-md",
        className
      )}
      onClick={onClick}
      type={type}
    >
      {children}
    </Button>
  );
}
