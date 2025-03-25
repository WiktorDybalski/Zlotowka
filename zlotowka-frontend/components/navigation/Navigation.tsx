"use client";

import {useEffect, useState} from "react";
import HamburgerMenu from "@/components/navigation/HamburgerMenu";
import UserInfo from "@/components/navigation/UserInfo";
import MobileMenu from "@/components/navigation/MobileMenu";
import DesktopMenu from "@/components/navigation/DesktopMenu";

export default function Navigation() {
  const [isOpen, setIsOpen] = useState(false);
  const navLinks: string[] = ["Dashboard", "Transakcje", "Marzenia"];
  const baseClasses = "w-full flex flex-col justify-center px-8 bg-[#262626] transition-all duration-300 rounded-b-lg";
  const mobileClasses = isOpen ? "h-screen pb-6 absolute top-0 left-0" : "h-20";
  const desktopClasses = "lg:h-screen lg:py-4 lg:pb-8 lg:rounded-r-lg lg:rounded-bl-none";

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
      <MobileMenu isOpen={isOpen} navLinks={navLinks} />

      {/* Visible only on desktop */}
      <DesktopMenu navLinks={navLinks} />
    </div>
  );
}
