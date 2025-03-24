"use client";
import Image from "next/image";
import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import SettingsLink from "@/components/navigation/SettingsLink";
import NavigationLinks from "@/components/navigation/NavigationLinks";

export default function Sidebar() {
  const [isOpen, setIsOpen] = useState(false);
  const navLinks: string[] = [
    "Dashboard",
    "Transakcje",
    "Marzenia",
    "Podsumowanie",
  ];

  const mobileContentVariants = {
    hidden: {
      opacity: 0,
      x: -50,
      transition: {
        duration: 0.3
      }
    },
    visible: {
      opacity: 1,
      x: 0,
      transition: {
        duration: 0.3,
        delay: 0.2
      }
    }
  };

  return (
      <div className={`w-full 
        ${isOpen ? "h-screen pb-8 absolute top-0 left-0" : "h-20"} 
        flex flex-col px-4 bg-[#262626] 
        transition-all duration-300 
        rounded-b-lg 
        lg:h-screen lg:py-4 lg:px-6 lg:pb-8
        lg:rounded-r-lg lg:rounded-bl-none`}>
        {/* Avatar Section - Always Visible */}
        <div className="flex justify-between items-center py-6">
          {/* Hamburger Menu - Mobile Only */}
          <div className="flex flex-col gap-y-2 lg:hidden cursor-pointer" onClick={() => setIsOpen(!isOpen)}>
            {[1, 2, 3].map((line) => (
                <span
                    key={line}
                    className="w-8 border-2 border-neutral-100"
                ></span>
            ))}
          </div>

          {/* User Info */}
          <div className="flex items-center">
            <div className="hidden lg:block mr-3">
              <Image
                  src="/avatar.png"
                  width={70}
                  height={70}
                  alt="Avatar"
                  className="rounded-full"
              />
            </div>
            <div>
              <h3 className="text-2xl">Kamil Rudny</h3>
              <h4 className="hidden lg:block text-neutral-400 text-sm">
                kamil.rudny@gmail.com
              </h4>
            </div>
          </div>
        </div>

        {/* Mobile menu */}
        <AnimatePresence>
          {isOpen && (
              <motion.div
                  className="lg:hidden w-full h-full flex flex-col overflow-hidden"
                  variants={mobileContentVariants}
                  initial="hidden"
                  animate="visible"
                  exit="hidden"
              >
                <div className="border-b border-neutral-100 my-4"></div>
                <NavigationLinks links={navLinks} isMobile={true}/>
                <div className="border-t border-neutral-100 my-4"></div>
                <motion.div
                    initial={{opacity: 0, y: 20}}
                    animate={{
                      opacity: 1,
                      y: 0,
                      transition: {delay: 0.6}
                    }}
                >
                  <SettingsLink/>
                </motion.div>
              </motion.div>
          )}
        </AnimatePresence>

        {/* Desktop menu */}
        <div className="hidden lg:flex lg:flex-col w-full h-full">
          <div className="border-b border-neutral-100 my-4"></div>
          <NavigationLinks links={navLinks}/>
          <div className="border-t border-neutral-100 my-4"></div>
          <SettingsLink/>
        </div>
      </div>
  );
}