import Image from "next/image";

const waves_dim = {
  width: 1117,
  height: 154,
};

export default function AbsoluteWaves({
  children,
}: {
  children?: React.ReactNode;
}) {
  return (
    <div className="absolute inset-0">
      <Image
        src="/assets/wave2.svg"
        alt="Top wave"
        {...waves_dim}
        className="w-3/4 top-0 left-0 max-w-3xl absolute"
      />
      <Image
        src="/assets/wave1.svg"
        alt="Top wave"
        {...waves_dim}
        className="w-3/4 top-0 left-0 max-w-3xl absolute"
      />
      <Image
        src="/assets/wave3.svg"
        alt="Bottom wave"
        {...waves_dim}
        className="w-3/4 right-0 bottom-0 max-w-3xl absolute"
      />

      {children}
    </div>
  );
}
