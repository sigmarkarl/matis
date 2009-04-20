class HelloController < ApplicationController
  def index
  end

  def show
  end

  def test
    bbuffer = java.nio.ByteBuffer.allocate( 150000 )
    #bbuffer.order( java.nio.ByteOrder.nativeOrder() )
    buffer = bbuffer.array()
    url = java.net.URL.new( "file:///C:\\IUF.SIB.ChemicalHazards.pdf" )
    stream = url.openStream()
    len = stream.read( buffer )
    total = 0
    while len > 0
      total += len
      len = stream.read( buffer, total, buffer.length-total )
    end
    #location = java.lang.String.new( buffer ) #request.env["SERVER_ADDR"]
    string = String.from_java_bytes buffer
    send_data( string, :type => "application/pdf", :disposition => "inline", :filename => "simmi.pdf" )
  end

  def stuff
    f = open( "C:\\IUF.SIB.ChemicalHazards.pdf", "r" )
    str = f.read()
    render :pdf => str
    f.close()
  end

end
