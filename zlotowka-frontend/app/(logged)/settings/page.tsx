"use client";

import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import DarkButton from "@/components/DarkButton";
import Image from "next/image";
import {JSX, useState} from "react";

interface AccountOption {
  text: string;
  avatar?: JSX.Element;
  value?: string;
  onClick?: () => void;
}

interface AccountFieldProps {
  text: string;
  value?: string;
  avatar?: JSX.Element;
  onClick?: () => void;
}

const AccountOptions: AccountOption[] = [
  {
    text: "Zdjęcie",
    avatar: <Image src="/avatar.png" width={45} height={45} alt="Avatar" className="rounded-full" />,
    onClick: () => {},
  },
  {
    text: "Nazwa użytkownika",
    value: "Kamil Rudny",
    onClick: () => {},
  },
  {
    text: "E-mail",
    value: "krudny@gmail.com",
    onClick: () => {},
  },
  {
    text: "Numer telefonu",
    value: "+48 123 456 789",
    onClick: () => {},
  },
];

const AccountField = ({ text, value, avatar, onClick = () => {} }: AccountFieldProps) => (
    <div className="grid grid-cols-[1fr_1fr_1fr] w-full py-4 border-t-2 border-dashed border-neutral-200 last:border-b-2">
      <div className="flex justify-left items-center">
        <h3 className="text-lg">{text}</h3>
      </div>
      <div className="flex justify-center items-center">
        {avatar && <div>{avatar}</div>}
        {value && <span className="text-neutral-600 text-lg">{value}</span>}
      </div>
      <div className="flex justify-end items-center">
        <div className="w-28 h-10">
          <DarkButton text={"Edytuj"} onClick={onClick} />
        </div>
      </div>
    </div>
);


const scrollToSection = (id: string) => {
  document.getElementById(id)?.scrollIntoView({ behavior: "smooth" });
};

export default function Settings() {
  const navLinks: string[] = ["Konto", "Preferencje", "Powiadomienia"];
  const [darkMode, setDarkMode] = useState(false);

  return (
      <div className="w-full min-h-screen px-32 py-20 font-semibold text-neutral-800">
        <div>
          <h1 className="text-6xl">Ustawienia</h1>
        </div>
        <div className="flex gap-x-6 mt-8">
          {navLinks.map(link => (
              <div
                  key={link}
                  className="px-6 py-2 bg-neutral-200 rounded-lg hover:cursor-pointer hover:bg-neutral-300 transition-all ease-in-out duration-200"
                  onClick={() => scrollToSection(link)}
              >
                <p className="text-lg">{link}</p>
              </div>
          ))}
        </div>

        <GenericCard className="mt-10 max-w-3xl p-6" id="Konto">
          <h2 className="text-3xl">Informacje ogólne</h2>
          <div className="mt-4">
            {AccountOptions.map((option, index) => (
                <AccountField
                    key={index}
                    text={option.text}
                    value={option.value}
                    avatar={option.avatar}
                    onClick={option.onClick}
                />
            ))}
          </div>
        </GenericCard>

        <GenericCard className="mt-10 max-w-3xl p-6" id={"Preferencje"}>
          <h2 className="text-3xl">Preferencje</h2>
          <div className="mt-4">
            <div className="flex justify-between items-center">
              <h4 className="text-lg">Ciemny motyw</h4>
              <div className="w-28 h-10">
                <DarkButton
                    className={`${
                        darkMode ? "bg-neutral-800 text-neutral-100" : "bg-neutral-200 text-neutral-800"
                    }`}
                    text={darkMode ? "Włączony" : "Wyłączony"}
                    onClick={() => setDarkMode(!darkMode)}
                />
              </div>
            </div>
          </div>
        </GenericCard>

        <GenericCard className="mt-10 max-w-3xl p-6" id={"Powiadomienia"}>
          <h2 className="text-3xl">Powiadomienia</h2>
          <div className="mt-4">
            <div className="flex justify-between items-center border-t-2 border-dashed border-neutral-200 py-4">
              <h4 className="text-lg">Na email</h4>
              <div className="w-28 h-10">
                <DarkButton
                    className={`${
                        darkMode ? "bg-neutral-800 text-neutral-100" : "bg-neutral-200 text-neutral-800"
                    }`}
                    text={darkMode ? "Włączony" : "Wyłączony"}
                    onClick={() => setDarkMode(!darkMode)}
                />
              </div>
            </div>
            <div className="flex justify-between items-center border-y-2 border-dashed border-neutral-200 py-4">
              <h4 className="text-lg">Na telefon</h4>
              <div className="w-28 h-10">
                <DarkButton
                    className={`${
                        darkMode ? "bg-neutral-800 text-neutral-100" : "bg-neutral-200 text-neutral-800"
                    }`}
                    text={darkMode ? "Włączony" : "Wyłączony"}
                    onClick={() => setDarkMode(!darkMode)}
                />
              </div>
            </div>
          </div>
        </GenericCard>
      </div>
  );
}
