// TODO: pewnie brakuje onClick i osobnego interfejsu do tego
export default function DarkButton({
  icon,
  text,
}: {
  icon: string;
  text: string;
}) {
  return (
    <button className="w-full h-full bg-[#262626] rounded-[10px] flex justify-center items-center text-neutral-100 py-2 text-sm gap-x-2 transition duration-200 ease-in-out hover:bg-[#141414] hover:cursor-pointer">
      <span className="material-symbols-outlined">{icon}</span>
      <h3>{text}</h3>
    </button>
  );
}
