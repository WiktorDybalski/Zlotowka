import NavigationLinks from "@/components/navigation/NavigationLinks";
import BottomLinks from "@/components/navigation/BottomLinks";
import { MenuProps } from "@/interfaces/navigation/MenuProps";
import Divider from "@/components/navigation/Divider";

export default function DesktopMenu({ navLinks }: MenuProps) {
  return (
    <div className="hidden xl:flex xl:flex-col w-full h-full">
      <Divider />
      <NavigationLinks links={navLinks} />
      <Divider />
      <BottomLinks />
    </div>
  );
}
