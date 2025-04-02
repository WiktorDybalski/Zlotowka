// TODO: pewnie brakuje onClick i osobnego interfejsu do tego
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
    <button className={`w-full h-full bg-[#262626] rounded-[10px] flex justify-center items-center text-neutral-100 py-2 text-sm gap-x-2 transition duration-200 ease-in-out hover:cursor-pointer ${className}`} onClick={onClick}>
      {icon && <span className="material-symbols">{icon}</span>}
      <h3>{text}</h3>
    </button>
  );
}
