import Link from "next/link";
import clsx from "clsx";

interface LightLinkProps {
  href: string;
  children?: React.ReactNode;
  className?: string;
}

export default function LightLink({
  href,
  children,
  className,
}: LightLinkProps) {
  return (
    <Link
      href={href}
      className={clsx("text-backgroundLightDark hover:underline", className)}
    >
      {children}
    </Link>
  );
}
