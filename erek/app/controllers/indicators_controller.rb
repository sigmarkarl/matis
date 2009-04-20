class IndicatorsController < ApplicationController
  # GET /indicators
  # GET /indicators.xml
  def index
    @indicators = Indicator.all

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @indicators }
    end
  end

  # GET /indicators/1
  # GET /indicators/1.xml
  def show
    @indicator = Indicator.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.xml  { render :xml => @indicator }
    end
  end

  # GET /indicators/new
  # GET /indicators/new.xml
  def new
    @indicator = Indicator.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @indicator }
    end
  end

  # GET /indicators/1/edit
  def edit
    @indicator = Indicator.find(params[:id])
  end

  # POST /indicators
  # POST /indicators.xml
  def create
    @indicator = Indicator.new(params[:indicator])

    respond_to do |format|
      if @indicator.save
        flash[:notice] = 'Indicator was successfully created.'
        format.html { redirect_to(@indicator) }
        format.xml  { render :xml => @indicator, :status => :created, :location => @indicator }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @indicator.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /indicators/1
  # PUT /indicators/1.xml
  def update
    @indicator = Indicator.find(params[:id])

    respond_to do |format|
      if @indicator.update_attributes(params[:indicator])
        flash[:notice] = 'Indicator was successfully updated.'
        format.html { redirect_to(@indicator) }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @indicator.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /indicators/1
  # DELETE /indicators/1.xml
  def destroy
    @indicator = Indicator.find(params[:id])
    @indicator.destroy

    respond_to do |format|
      format.html { redirect_to(indicators_url) }
      format.xml  { head :ok }
    end
  end
end
