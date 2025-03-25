import Image from "next/image";

export default function UserInfo() {
  return (
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
  );
}
