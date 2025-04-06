export default function CardNumber({ text, className }: { text: string; className?: string }) {
  return <p className={`font-(family-name:--font-lato) ${className ? className : ''}`}>{text}</p>;
}