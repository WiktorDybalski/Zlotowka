import { NavigationLinksProps } from "@/interfaces/navigation/NavigationLinksProps";

export interface MenuProps {
  isOpen?: boolean;
  navLinks: NavigationLinksProps["links"];
  onLinkClick?: () => void;
}
