"use client";

import { useEffect, useState } from "react";
import HamburgerMenu from "@/components/navigation/HamburgerMenu";
import UserInfo from "@/components/navigation/UserInfo";
import MobileMenu from "@/components/navigation/MobileMenu";
import DesktopMenu from "@/components/navigation/DesktopMenu";

export default function Navigation() {
  const [isOpen, setIsOpen] = useState(false);
  const navLinks: string[] = ["Dashboard", "Transakcje", "Marzenia"];
  const baseClasses =
      "w-full flex flex-col justify-center px-6 bg-accent transition-all duration-300 xl:rounded-b-lg z-[999]";
  const mobileClasses = isOpen
      ? "h-[100dvh] pb-6 absolute top-0 left-0"
      : "h-20";
  const desktopClasses =
      "xl:h-screen xl:py-4 xl:pb-8 xl:rounded-r-lg xl:rounded-bl-none";

  const closeMenu = () => {
    setIsOpen(false);
  };

  useEffect(() => {
    if (isOpen) {
      document.body.classList.add("overflow-hidden");
    } else {
      document.body.classList.remove("overflow-hidden");
    }

    return () => {
      document.body.classList.remove("overflow-hidden");
    };
  }, [isOpen]);

  return (
      <div className={`${baseClasses} ${mobileClasses} ${desktopClasses}`}>
        {/* Always Visible */}
        <div className="flex justify-between items-center py-6">
          <HamburgerMenu isOpen={isOpen} onClick={() => setIsOpen(!isOpen)} />
          <UserInfo />
        </div>

        {/* Visible only on mobile */}
        <MobileMenu isOpen={isOpen} navLinks={navLinks} onLinkClick={closeMenu} />

        {/* Visible only on desktop */}
        <DesktopMenu navLinks={navLinks} />
      </div>
  );
}