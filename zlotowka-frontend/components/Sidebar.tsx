"use client";
import Image from "next/image";
import { useState } from "react";

export default function Sidebar() {
  const [isOpen, setIsOpen] = useState(false);
  const navLinks: string[] = [
    "Dashboard",
    "Transakcje",
    "Marzenia",
    "Podsumowanie",
  ];

  return (
      <div className="w-full h-full flex flex-col bg-[#262626] select-none font-medium text-neutral-100 py-4 px-6 rounded-b-lg lg:py-8 lg:rounded-r-lg lg:rounded-b-none">
        {/* Avatar */}
        <div className="h-10 flex justify-between items-center lg:h-24 lg:justify-center">
          <div className="h-full flex items-center">
            <div className="hidden lg:block">
              <Image src="/avatar.png" width={70} height={70} alt="Avatar" />
            </div>
            <div className="lg:ml-3">
              <h3 className="text-xl font-semibold lg:text-2xl">Kamil Rudny</h3>
              <h4 className="hidden lg:block">kamil.rudny@gmail.com</h4>
            </div>
          </div>
          {/* Hamburger */}
          <div
              className="flex flex-col gap-y-[6px] cursor-pointer lg:hidden"
              onClick={() => setIsOpen(!isOpen)}
          >
            <span className="w-8 border-2 border-neutral-100"></span>
            <span className="w-8 border-2 border-neutral-100"></span>
            <span className="w-8 border-2 border-neutral-100"></span>
          </div>
        </div>

        {/* Horizontal Line */}
        <div className="hidden lg:block lg:border-b border-neutral-100 my-6"></div>

        {/* Links */}
        <div
            className={`overflow-hidden transition-[max-height] duration-300 ease-linear ${
                isOpen ? "max-h-96" : "max-h-0"
            } lg:max-h-none lg:block`}
        >
          {navLinks.map((link) => (
              <div key={link} className="border-l-4 border-neutral-100 my-6 lg:my-6">
                <p className="mx-3 text-md lg:text-xl lg:mx-8">{link}</p>
              </div>
          ))}
        </div>

        {/* Horizontal Line */}
        <div className="hidden lg:block lg:border-b border-neutral-100 lg:mt-auto my-6"></div>

        {/* Bottom Links */}
        <div
            className={`overflow-hidden transition-[max-height] duration-300 ease-linear ${
                isOpen ? "max-h-40" : "max-h-0"
            } lg:max-h-none lg:block`}
        >
          <div className="flex items-center">
          <span className="hidden material-symbols-outlined lg:block">
            <p className="hidden lg:block text-2xl">settings</p>
          </span>
            <div className="border-l-4 border-neutral-100 lg:my-2 lg:border-0">
              <p className="mx-3 text-md lg:text-xl lg:mx-4">Ustawienia</p>
            </div>
          </div>
        </div>
      </div>
  );
}
