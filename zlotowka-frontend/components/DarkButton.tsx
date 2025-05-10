import {clsx} from "clsx";

export default function DarkButton({
  icon,
  text,
  onClick,
  className,
}: {
  icon?: string;
  text: string;
  onClick: () => void;
  className?: string;
}) {
  return (
    <button
      className={clsx(` ${className} w-full h-full bg-accent rounded-[8px] flex justify-center items-center text-background py-2 text-sm gap-x-2 transition duration-200 ease-in-out hover:cursor-pointer`)}
      onClick={onClick}
    >
      {icon && <span className="material-symbols">{icon}</span>}
      <h3>{text}</h3>
    </button>
  );
}
