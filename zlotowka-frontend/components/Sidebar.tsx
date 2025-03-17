import Image from 'next/image';

export default function Sidebar() {
  return (
      <div className="w-full h-full bg-neutral-800 text-neutral-100 flex flex-col py-6 gap-y-6">

        <div className="w-full">
          <div className="flex justify-center gap-4">
            <div>
              <Image src="/avatar.png" width={70} height={70} alt="Avatar" />
            </div>
            <div className="flex flex-col justify-center">
              <p className="m-0 p-0 text-2xl">Kamil Rudny</p>
              <p className="m-0 p-0 text-md">kamil.rudny@gmail.com</p>
            </div>
          </div>
        </div>
        <div className="border-neutral-100 border-solid border-t-[1px] mx-5"></div>
        <div className="w-full bg-red-400">
        links
        </div>
        <div className="border-neutral-100 border-solid border-t-[1px] mt-auto mx-5"></div>
        <div className="w-full bg-red-200">
          help
        </div>
      </div>
  )
}