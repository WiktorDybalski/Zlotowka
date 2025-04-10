import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import { TextNumberFieldProps } from "@/interfaces/dashboard/cards/TextNumberFieldProps";

export default function TextNumberField({
  text,
  number,
}: TextNumberFieldProps) {
  return (
    <div className="w-full flex justify-between">
      <CardText text={text} />
      <CardNumber text={number} />
    </div>
  );
}
