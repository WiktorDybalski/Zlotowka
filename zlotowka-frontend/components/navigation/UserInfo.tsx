import Image from "next/image";
import toast from "react-hot-toast";

export default function UserInfo() {
  return (
    <div className="flex items-center justify-between w-full">
      <div className="hidden xl:block mr-3 ">
        <Image
          src="/avatar.png"
          width={65}
          height={65}
          alt="Avatar"
          className="rounded-full"
        />
      </div>
      <div>
        <h3 className="text-xl ml-3 xl:ml-0">Kamil Rudny</h3>
        <h4 className="hidden xl:block text-neutral-400 text-xs">
          kamil.rudny@gmail.com
        </h4>
      </div>
      <div className="flex items-center ml-3 group hover:cursor-pointer">
        <span
            className="material-symbols text-background transition-colors duration-200 group-hover:text-yellow-500"
            onClick={() => toast.success("Jakieś ciekawe powiadomienie")}
        >
          notifications
        </span>
      </div>
    </div>
  );
}
