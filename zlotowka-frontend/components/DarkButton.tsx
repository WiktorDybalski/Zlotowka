import { clsx } from "clsx";

export default function DarkButton({
  icon,
  text,
  onClick,
  className,
  disabled = false,
}: {
  icon?: string;
  text?: string;
  onClick: () => void;
  className?: string;
  disabled?: boolean;
}) {
  return (
    <button
      className={clsx(
        ` ${className} w-full h-full bg-accent hover:bg-veryDark rounded-[8px] flex justify-center items-center text-background py-2 text-sm gap-x-2 transition duration-200 ease-in-out hover:cursor-pointer disabled:cursor-not-allowed disabled:bg-accent/50 disabled:text-background/50`
      )}
      onClick={onClick}
      disabled={disabled}
    >
      {icon && <span className="material-symbols">{icon}</span>}
      {text && <h3 className={icon ? "pr-2" : ""}>{text}</h3>}
    </button>
  );
}
