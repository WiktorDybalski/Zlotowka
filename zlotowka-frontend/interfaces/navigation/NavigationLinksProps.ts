export interface NavigationLinksProps {
  links: Array<{ displayName: string; href: string }>;
  isMobile?: boolean;
  onLinkClick?: () => void;
}
