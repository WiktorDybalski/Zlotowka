import clsx from "clsx";

interface HStackProps {
  children: React.ReactNode;
  className?: string;
}

export default function HStack({ children, className }: HStackProps) {
  return (
    <div
      className={clsx(
        "flex flex-col items-center", // Ustawia elementy w kolumnie i centruje na osi X
        className // Dodatkowe klasy przekazane jako props
      )}
    >
      {children}
    </div>
  );
}
