import Link from "next/link";
import clsx from "clsx";
import React from "react";

interface LightLinkProps {
  href: string;
  children?: React.ReactNode;
  className?: string;
  disabled?: boolean;
}

export default function LightLink({
  href,
  children,
  className,
  disabled = false,
}: LightLinkProps) {
  if (disabled) {
    return (
      <span
        className={clsx(
          "text-backgroundLightDark cursor-not-allowed",
          className,
        )}
      >
        {children}
      </span>
    );
  }
  return (
    <Link
      href={href}
      className={clsx("text-backgroundLightDark hover:underline", className)}
    >
      {children}
    </Link>
  );
}
