import ReturnButtonSVG from "@/components/resposive_svg/ReturnButtonSVG";
import Link from "next/link";

interface ReturnButtonProps {
  position?: "left" | "begone";
  onClick?: () => void;
  href?: string;
  refererPrivilege?: boolean; // if true, use the referer as the href if available
}

export default function ReturnButton({
  position = "left",
  onClick,
  href,
}: ReturnButtonProps) {
  if (position === "begone") {
    return null; // do not render
  }

  return (
    <button
      onClick={onClick}
      className="hidden md:block absolute top-1/2 lg:left-1/10 left-10 transform -translate-y-1/2 hover:scale-110 transition duration-200 ease-in-out hover:cursor-pointer"
    >
      {href ? (
        <Link href={href}>
          <ReturnButtonSVG />
        </Link>
      ) : (
        <ReturnButtonSVG />
      )}
    </button>
  );
}
