// TODO: pewnie brakuje onClick i osobnego interfejsu do tego
export default function DarkButton({
  icon,
  text,
  onClick,
}: {
  icon?: string;
  text: string;
  onClick: () => void;
}) {
  return (
    <button className="w-full h-full bg-[#262626] rounded-[10px] flex justify-center items-center text-neutral-100 py-2 text-sm gap-x-2 transition duration-200 ease-in-out hover:bg-[#141414] hover:cursor-pointer" onClick={onClick}>
      {icon && <span className="material-symbols">{icon}</span>}
      <h3>{text}</h3>
    </button>
  );
}
