import Image from 'next/image';

export default function Sidebar() {
  const navLinks: string[] = ["Dashboard", "Transakcje", "Marzenia", "Podsumowanie"]

  return (
      <div className="w-full h-full bg-neutral-800 text-neutral-100 flex flex-col py-8 gap-y-6 font-medium select-none">
        {/* Avatar */}
        <div className="w-full">
          <div className="flex justify-center gap-4">
            <div>
              <Image src="/avatar.png" width={70} height={70} alt="Avatar" />
            </div>
            <div className="flex flex-col justify-center">
              <h3 className="text-2xl">Kamil Rudny</h3>
              <h4 className="text-md">kamil.rudny@gmail.com</h4>
            </div>
          </div>
        </div>

        {/* Horizontal Line */}
        <div className="border-neutral-100 border-solid border-t-[1px] mx-6"></div>

        {/* Links */}
        <div className="w-full">
          {navLinks.map((link) => (
              <div key={link} className="flex ml-6 my-6 border-l-4 border-neutral-100 hover:cursor-pointer">
                <p className="ml-8 text-xl">{link}</p>
              </div>
          ))}
        </div>

        {/* Horizontal Line */}
        <div className="border-neutral-100 border-solid border-t-[1px] mt-auto mx-6"></div>

        {/* Bottom Links */}
        <div className="w-full">
          <div className="flex items-center ml-6 hover:cursor-pointer">
            <span className="material-symbols-outlined"><p className="font-light text-3xl">settings</p></span>
            <p className="ml-6 text-xl">Ustawienia</p>
          </div>
        </div>
      </div>
  )
}