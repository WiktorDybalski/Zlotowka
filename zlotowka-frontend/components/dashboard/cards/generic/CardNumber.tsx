export default function CardNumber({
  text,
  className,
}: {
  text: string;
  className?: string;
}) {
  return <p className={`font-lato ${className ? className : ""}`}>{text}</p>;
}
