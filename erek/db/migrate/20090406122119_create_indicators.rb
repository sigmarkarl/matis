class CreateIndicators < ActiveRecord::Migration
  def self.up
    create_table :indicators do |t|
      t.string :code
      t.string :name

      t.timestamps
    end
  end

  def self.down
    drop_table :indicators
  end
end
