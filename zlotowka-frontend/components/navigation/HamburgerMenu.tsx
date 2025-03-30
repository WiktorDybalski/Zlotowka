import { HamburgerMenuProps } from "@/interfaces/navigation/HamburgerMenuProps";

export default function HamburgerMenu({ isOpen, onClick }: HamburgerMenuProps) {
  return (
    <button
      aria-label={isOpen ? "Zamknij menu" : "Otwórz menu"}
      aria-expanded={isOpen}
      onClick={onClick}
      className="xl:hidden"
    >
      <svg
        width="32"
        height="24"
        viewBox="0 0 32 24"
        className="fill-current text-neutral-100"
      >
        <path d="M0 2h32v3H0z" />
        <path d="M0 10h32v3H0z" />
        <path d="M0 18h32v3H0z" />
      </svg>
    </button>
  );
}
