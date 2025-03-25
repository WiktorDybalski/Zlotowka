import NavigationLinks from "@/components/navigation/NavigationLinks";
import SettingsLink from "@/components/navigation/SettingsLink";
import { MenuProps } from "@/interfaces/navigation/MenuProps";
import Divider from "@/components/navigation/Divider";

export default function DesktopMenu({ navLinks }: MenuProps) {
  return (
    <div className="hidden lg:flex lg:flex-col w-full h-full">
      <Divider />
      <NavigationLinks links={navLinks} />
      <Divider />
      <SettingsLink />
    </div>
  );
}
